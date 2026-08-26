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
  AdminAuthService
} from '../../services/admin-auth.service';

import {
  InternshipOfferService
} from '../../services/internship-offer.service';
import { AdminInternshipApplicationService } from '../../services/admin-internship-application.service';


@Component({
  selector: 'app-admin-dashboard',

  standalone: true,

  imports: [
    CommonModule,
    RouterLink
  ],

  templateUrl:
    './admin-dashboard.html',

  styleUrl:
    './admin-dashboard.css'
})
export class AdminDashboard
  implements OnInit {

  username =
    signal('');

  loading =
    signal(true);

  totalOffers =
    signal(0);

  openOffers =
    signal(0);

  closedOffers =
    signal(0);

  totalApplications =
    signal(0);


  constructor(
    private adminAuthService:
      AdminAuthService,

    private internshipOfferService:
      InternshipOfferService,

    private applicationService:
      AdminInternshipApplicationService,

    private router:
      Router
  ) {}


  ngOnInit(): void {

    this.loadAdmin();

    this.loadOfferStatistics();

    this.loadApplicationStatistics();
  }


  loadApplicationStatistics(): void {
    this.applicationService
      .getApplicationCount()
      .subscribe({
        next: response =>
          this.totalApplications.set(response.count),
        error: error =>
          console.error('Could not load applications:', error)
      });
  }


  /* =========================
     ADMIN
  ========================= */

  loadAdmin(): void {

    this.adminAuthService
      .getCurrentAdmin()
      .subscribe({

        next: (admin) => {

          if (
            !admin.authenticated
          ) {

            this.router.navigate([
              '/admin/login'
            ]);

            return;
          }


          this.username.set(
            admin.username ?? 'Admin'
          );

          this.loading.set(false);
        },


        error: () => {

          this.router.navigate([
            '/admin/login'
          ]);
        }

      });
  }


  /* =========================
     OFFER STATISTICS
  ========================= */

  loadOfferStatistics(): void {

    this.internshipOfferService
      .getAllOffers()
      .subscribe({

        next: (offers) => {

          this.totalOffers.set(
            offers.length
          );


          this.openOffers.set(
            offers.filter(
              offer =>
                offer.status === 'OPEN'
            ).length
          );


          this.closedOffers.set(
            offers.filter(
              offer =>
                offer.status === 'CLOSED'
            ).length
          );
        },


        error: (error) => {

          console.error(
            'Could not load offers:',
            error
          );
        }

      });
  }


  /* =========================
     LOGOUT
  ========================= */

  logout(): void {

    this.adminAuthService
      .logout()
      .subscribe({

        next: () => {

          this.router.navigate([
            '/admin/login'
          ]);
        },


        error: () => {

          this.router.navigate([
            '/admin/login'
          ]);
        }

      });
  }
}
