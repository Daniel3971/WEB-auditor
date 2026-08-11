import {
  Component,
  OnInit,
  signal
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  Router,
  RouterLink
} from '@angular/router';

import {
  InternshipOffer
} from '../../models/internship-offer';

import {
  AdminInternshipOfferService
} from '../../services/admin-internship-offer.service';

import {
  AdminAuthService
} from '../../services/admin-auth.service';


@Component({
  selector: 'app-admin-offers',

  standalone: true,

  imports: [
    CommonModule,
    RouterLink
  ],

  templateUrl:
    './admin-offers.html',

  styleUrl:
    './admin-offers.css'
})
export class AdminOffers
  implements OnInit {

  offers =
    signal<InternshipOffer[]>([]);

  loading =
    signal(true);

  errorMessage =
    signal('');

  searchTerm =
    signal('');

  statusFilter =
    signal('ALL');


  constructor(
    private offerService:
      AdminInternshipOfferService,

    private adminAuthService:
      AdminAuthService,

    private router:
      Router
  ) {}


  ngOnInit(): void {
    this.loadOffers();
  }


  loadOffers(): void {

    this.loading.set(true);

    this.errorMessage.set('');


    this.offerService
      .getAllOffers()
      .subscribe({

        next: offers => {

          this.offers.set(
            offers
          );

          this.loading.set(false);
        },


        error: error => {

          console.error(error);

          this.loading.set(false);

          this.errorMessage.set(
            'Could not load internship offers.'
          );
        }

      });
  }


  get filteredOffers():
    InternshipOffer[] {

    const search =
      this.searchTerm()
        .trim()
        .toLowerCase();

    const status =
      this.statusFilter();


    return this.offers()
      .filter(
        offer => {

          const matchesSearch =
            !search ||
            offer.titleEn
              .toLowerCase()
              .includes(search) ||
            offer.titleAr
              .toLowerCase()
              .includes(search);


          const matchesStatus =
            status === 'ALL' ||
            offer.status === status;


          return (
            matchesSearch &&
            matchesStatus
          );
        }
      );
  }


  updateSearch(
    event: Event
  ): void {

    const input =
      event.target as HTMLInputElement;

    this.searchTerm.set(
      input.value
    );
  }


  updateFilter(
    event: Event
  ): void {

    const select =
      event.target as HTMLSelectElement;

    this.statusFilter.set(
      select.value
    );
  }


  toggleStatus(
    offer: InternshipOffer
  ): void {

    const nextStatus =
      offer.status === 'OPEN'
        ? 'CLOSED'
        : 'OPEN';


    this.offerService
      .changeStatus(
        offer.id,
        nextStatus
      )
      .subscribe({

        next: updatedOffer => {

          this.offers.update(
            offers =>
              offers.map(
                current =>
                  current.id ===
                  updatedOffer.id
                    ? updatedOffer
                    : current
              )
          );
        },


        error: error => {

          this.errorMessage.set(
            error?.error?.message ||
            'Could not change offer status.'
          );
        }

      });
  }


  logout(): void {

    this.adminAuthService
      .logout()
      .subscribe({

        next: () =>
          this.router.navigate([
            '/admin/login'
          ]),

        error: () =>
          this.router.navigate([
            '/admin/login'
          ])

      });
  }
}