import {
  Component,
  signal
} from '@angular/core';

import {
  FormsModule
} from '@angular/forms';

import {
  Router,
  RouterLink
} from '@angular/router';

import {
  AdminAuthService
} from '../../services/admin-auth.service';

import {
  AdminInternshipOfferService,
  AdminInternshipOfferRequest
} from '../../services/admin-internship-offer.service';


@Component({
  selector: 'app-admin-create-offer',

  standalone: true,

  imports: [
    FormsModule,
    RouterLink
  ],

  templateUrl:
    './admin-create-offer.html',

  styleUrl:
    './admin-create-offer.css'
})
export class AdminCreateOffer {

  titleEn = '';

  titleAr = '';

  descriptionEn = '';

  descriptionAr = '';

  missionEn = '';

  missionAr = '';

  profileEn = '';

  profileAr = '';

  duration = '';

  applicationDeadline = '';

  maxCandidates = 50;

  status: 'OPEN' | 'CLOSED' =
    'OPEN';


  submitting =
    signal(false);

  errorMessage =
    signal('');


  constructor(
    private offerService:
      AdminInternshipOfferService,

    private adminAuthService:
      AdminAuthService,

    private router:
      Router
  ) {}


  createOffer(): void {

    this.errorMessage.set('');


    if (
      !this.titleEn.trim() ||
      !this.titleAr.trim() ||
      !this.descriptionEn.trim() ||
      !this.descriptionAr.trim() ||
      !this.missionEn.trim() ||
      !this.missionAr.trim() ||
      !this.profileEn.trim() ||
      !this.profileAr.trim() ||
      !this.duration.trim() ||
      !this.applicationDeadline
    ) {

      this.errorMessage.set(
        'Please complete all required fields.'
      );

      return;
    }


    if (
      this.maxCandidates < 1
    ) {

      this.errorMessage.set(
        'Maximum candidates must be at least 1.'
      );

      return;
    }


    const request:
      AdminInternshipOfferRequest = {

      titleEn:
        this.titleEn.trim(),

      titleAr:
        this.titleAr.trim(),

      descriptionEn:
        this.descriptionEn.trim(),

      descriptionAr:
        this.descriptionAr.trim(),

      missionEn:
        this.missionEn.trim(),

      missionAr:
        this.missionAr.trim(),

      profileEn:
        this.profileEn.trim(),

      profileAr:
        this.profileAr.trim(),

      duration:
        this.duration.trim(),

      applicationDeadline:
        this.applicationDeadline,

      maxCandidates:
        this.maxCandidates,

      status:
        this.status
    };


    this.submitting.set(true);


    this.offerService
      .createOffer(request)
      .subscribe({

        next: () => {

          this.submitting.set(false);

          this.router.navigate([
            '/admin/offers'
          ]);
        },


        error: (error) => {

          console.error(
            'Could not create offer:',
            error
          );

          this.submitting.set(false);

          this.errorMessage.set(
            error?.error?.message ||
            'Could not create the internship offer.'
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