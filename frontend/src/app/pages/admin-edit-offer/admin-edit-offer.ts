import {
  Component,
  OnInit,
  signal
} from '@angular/core';

import {
  FormsModule
} from '@angular/forms';

import {
  ActivatedRoute,
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
  selector: 'app-admin-edit-offer',

  standalone: true,

  imports: [
    FormsModule,
    RouterLink
  ],

  templateUrl:
    './admin-edit-offer.html',

  styleUrl:
    './admin-edit-offer.css'
})
export class AdminEditOffer
  implements OnInit {

  offerId: number | null =
    null;


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

  maxCandidates = 1;

  currentCandidates = 0;

  status: 'OPEN' | 'CLOSED' =
    'OPEN';


  loading =
    signal(true);

  submitting =
    signal(false);

  errorMessage =
    signal('');

  successMessage =
    signal('');


  constructor(
    private route:
      ActivatedRoute,

    private router:
      Router,

    private offerService:
      AdminInternshipOfferService,

    private adminAuthService:
      AdminAuthService
  ) {}


  ngOnInit(): void {

    const id =
      Number(
        this.route.snapshot
          .paramMap
          .get('id')
      );


    if (
      !id ||
      Number.isNaN(id)
    ) {

      this.errorMessage.set(
        'Invalid internship offer.'
      );

      this.loading.set(false);

      return;
    }


    this.offerId = id;

    this.loadOffer();
  }


  /* =========================
     LOAD OFFER
  ========================= */

  loadOffer(): void {

    if (
      this.offerId === null
    ) {
      return;
    }


    this.loading.set(true);

    this.errorMessage.set('');


    this.offerService
      .getOffer(
        this.offerId
      )
      .subscribe({

        next: offer => {

          this.titleEn =
            offer.titleEn;

          this.titleAr =
            offer.titleAr;

          this.descriptionEn =
            offer.descriptionEn;

          this.descriptionAr =
            offer.descriptionAr;

          this.missionEn =
            offer.missionEn;

          this.missionAr =
            offer.missionAr;

          this.profileEn =
            offer.profileEn;

          this.profileAr =
            offer.profileAr;

          this.duration =
            offer.duration;

          this.applicationDeadline =
            offer.applicationDeadline;

          this.maxCandidates =
            offer.maxCandidates;

          this.currentCandidates =
            offer.currentCandidates;

          this.status =
            offer.status;


          this.loading.set(false);
        },


        error: error => {

          console.error(
            'Could not load offer:',
            error
          );

          this.loading.set(false);

          this.errorMessage.set(
            'Could not load the internship offer.'
          );
        }

      });
  }


  /* =========================
     SAVE
  ========================= */

  saveChanges(): void {

    if (
      this.offerId === null
    ) {
      return;
    }


    this.errorMessage.set('');

    this.successMessage.set('');


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


    if (
      this.maxCandidates <
      this.currentCandidates
    ) {

      this.errorMessage.set(
        `Maximum candidates cannot be lower than the current number of candidates (${this.currentCandidates}).`
      );

      return;
    }


    if (
      this.status === 'OPEN' &&
      this.currentCandidates >=
        this.maxCandidates
    ) {

      this.errorMessage.set(
        'This offer cannot be open because it has reached its maximum number of candidates.'
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
      .updateOffer(
        this.offerId,
        request
      )
      .subscribe({

        next: updatedOffer => {

          this.submitting.set(false);

          this.currentCandidates =
            updatedOffer.currentCandidates;

          this.status =
            updatedOffer.status;

          this.successMessage.set(
            'Internship offer updated successfully.'
          );
        },


        error: error => {

          console.error(
            'Could not update offer:',
            error
          );

          this.submitting.set(false);

          this.errorMessage.set(
            error?.error?.message ||
            'Could not update the internship offer.'
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