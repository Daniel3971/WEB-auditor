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

  viewingArchived =
    signal(false);


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


    const request = this.viewingArchived()
      ? this.offerService.getArchivedOffers()
      : this.offerService.getAllOffers();

    request
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

  showActiveOffers(): void {
    if (!this.viewingArchived()) return;
    this.viewingArchived.set(false);
    this.clearFiltersAndReload();
  }

  showArchivedOffers(): void {
    if (this.viewingArchived()) return;
    this.viewingArchived.set(true);
    this.clearFiltersAndReload();
  }

  private clearFiltersAndReload(): void {
    this.searchTerm.set('');
    this.statusFilter.set('ALL');
    this.loadOffers();
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

  removeOffer(offer: InternshipOffer): void {
    const confirmed = window.confirm(
      `Remove "${offer.titleEn}"? The offer will disappear from the website, but its applications will be preserved.`
    );

    if (!confirmed) {
      return;
    }

    this.errorMessage.set('');

    this.offerService.removeOffer(offer.id).subscribe({
      next: () => {
        this.offers.update(offers =>
          offers.filter(current => current.id !== offer.id)
        );
      },
      error: error => {
        this.errorMessage.set(
          error?.error?.message || 'Could not remove the internship offer.'
        );
      }
    });
  }

  restoreOffer(offer: InternshipOffer): void {
    this.errorMessage.set('');

    this.offerService.restoreOffer(offer.id).subscribe({
      next: () => {
        this.offers.update(offers =>
          offers.filter(current => current.id !== offer.id)
        );
      },
      error: error => {
        this.errorMessage.set(
          error?.error?.message || 'Could not restore the internship offer.'
        );
      }
    });
  }

  permanentlyDeleteOffer(offer: InternshipOffer): void {
    const confirmed = window.confirm(
      `Permanently delete "${offer.titleEn}"? This cannot be undone. Offers with applicants cannot be deleted.`
    );

    if (!confirmed) {
      return;
    }

    this.errorMessage.set('');

    this.offerService.permanentlyDeleteOffer(offer.id).subscribe({
      next: () => {
        this.offers.update(offers =>
          offers.filter(current => current.id !== offer.id)
        );
      },
      error: error => {
        this.errorMessage.set(
          error?.error?.message || 'Could not permanently delete the internship offer.'
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
