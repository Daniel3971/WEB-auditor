import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import {
  TranslatePipe,
  TranslateService
} from '@ngx-translate/core';

import { InternshipOffer } from '../../models/internship-offer';
import { InternshipOfferService } from '../../services/internship-offer.service';
import { InternshipApplicationService } from '../../services/internship-application.service';

@Component({
  selector: 'app-internship-offers',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    TranslatePipe
  ],
  templateUrl: './internship-offers.html',
  styleUrl: './internship-offers.css'
})
export class InternshipOffers implements OnInit {

  /* =========================
     OFFERS
  ========================= */

  offers = signal<InternshipOffer[]>([]);

  loading = signal(true);

  error = signal(false);


  /* =========================
     FILTERS
  ========================= */

  searchTerm = signal('');

  selectedStatus = signal('ALL');


  /* =========================
     SELECTED OFFER
  ========================= */

  selectedOffer = signal<InternshipOffer | null>(null);


  /* =========================
     APPLICATION FORM
  ========================= */

  showApplicationForm = signal(false);

  selectedFileName = signal('');

  selectedFile = signal<File | null>(null);

  fileError = signal('');

  submitting = signal(false);

  submitSuccess = signal(false);

  submitError = signal('');


  /* =========================
     CONSTRUCTOR
  ========================= */

  constructor(
    private internshipOfferService: InternshipOfferService,
    private internshipApplicationService: InternshipApplicationService,
    public translate: TranslateService
  ) {}


  /* =========================
     INITIALIZATION
  ========================= */

  ngOnInit(): void {
    this.loadOffers();
  }


  /* =========================
     LOAD OFFERS
  ========================= */

  loadOffers(): void {

    this.loading.set(true);

    this.error.set(false);

    this.internshipOfferService
      .getAllOffers()
      .subscribe({

        next: (offers) => {

          this.offers.set(offers);

          /*
           * If an offer is currently selected,
           * refresh it with the newest database values.
           */
          const currentSelected =
            this.selectedOffer();

          if (currentSelected) {

            const refreshedOffer =
              offers.find(
                offer =>
                  offer.id ===
                  currentSelected.id
              );

            if (refreshedOffer) {
              this.selectedOffer.set(
                refreshedOffer
              );
            }
          }

          this.loading.set(false);
        },

        error: (error) => {

          console.error(
            'Error loading internship offers:',
            error
          );

          this.error.set(true);

          this.loading.set(false);
        }

      });
  }


  /* =========================
     FILTERED OFFERS
  ========================= */

  get filteredOffers(): InternshipOffer[] {

    const search =
      this.searchTerm()
        .toLowerCase()
        .trim();

    const status =
      this.selectedStatus();


    return this.offers().filter(
      (offer) => {

        const title =
          this.getTitle(offer)
            .toLowerCase();

        const description =
          this.getDescription(offer)
            .toLowerCase();


        const matchesSearch =
          search === '' ||
          title.includes(search) ||
          description.includes(search);


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


  /* =========================
     LANGUAGE
  ========================= */

  isArabic(): boolean {

    return (
      this.translate.getCurrentLang() === 'ar'
    );
  }


  /* =========================
     TRANSLATED DATABASE DATA
  ========================= */

  getTitle(
    offer: InternshipOffer
  ): string {

    return this.isArabic()
      ? offer.titleAr
      : offer.titleEn;
  }


  getDescription(
    offer: InternshipOffer
  ): string {

    return this.isArabic()
      ? offer.descriptionAr
      : offer.descriptionEn;
  }


  getMission(
    offer: InternshipOffer
  ): string {

    return this.isArabic()
      ? offer.missionAr
      : offer.missionEn;
  }


  getProfile(
    offer: InternshipOffer
  ): string {

    return this.isArabic()
      ? offer.profileAr
      : offer.profileEn;
  }


  /* =========================
     SEARCH
  ========================= */

  updateSearch(
    event: Event
  ): void {

    const input =
      event.target as HTMLInputElement;

    this.searchTerm.set(
      input.value
    );
  }


  /* =========================
     STATUS FILTER
  ========================= */

  updateStatus(
    event: Event
  ): void {

    const select =
      event.target as HTMLSelectElement;

    this.selectedStatus.set(
      select.value
    );
  }


  /* =========================
     OPEN OFFER
  ========================= */

  openOffer(
    offer: InternshipOffer
  ): void {

    this.selectedOffer.set(
      offer
    );

    this.showApplicationForm.set(false);

    this.resetApplicationMessages();

    setTimeout(() => {

      document
        .getElementById(
          'offer-details'
        )
        ?.scrollIntoView({
          behavior: 'smooth',
          block: 'start'
        });

    }, 0);
  }


  /* =========================
     CLOSE OFFER
  ========================= */

  closeOffer(): void {

    this.selectedOffer.set(null);

    this.showApplicationForm.set(false);

    this.clearSelectedFile();

    this.resetApplicationMessages();
  }


  /* =========================
     AVAILABILITY
  ========================= */

  isAvailable(
    offer: InternshipOffer
  ): boolean {

    return (
      offer.status === 'OPEN' &&
      offer.currentCandidates <
        offer.maxCandidates
    );
  }


  getRemainingPlaces(
    offer: InternshipOffer
  ): number {

    return Math.max(
      0,
      offer.maxCandidates -
        offer.currentCandidates
    );
  }


  /* =========================
     OPEN APPLICATION FORM
  ========================= */

  openApplicationForm(): void {

    const offer =
      this.selectedOffer();

    if (!offer) {
      return;
    }

    if (!this.isAvailable(offer)) {
      return;
    }

    this.showApplicationForm.set(true);

    this.resetApplicationMessages();

    setTimeout(() => {

      document
        .getElementById(
          'internship-application'
        )
        ?.scrollIntoView({
          behavior: 'smooth',
          block: 'start'
        });

    }, 0);
  }


  /* =========================
     CLOSE APPLICATION FORM
  ========================= */

  closeApplicationForm(): void {

    this.showApplicationForm.set(false);

    this.clearSelectedFile();

    this.resetApplicationMessages();
  }


  /* =========================
     CV
  ========================= */

  onFileSelected(
    event: Event
  ): void {

    const input =
      event.target as HTMLInputElement;

    const file =
      input.files?.[0];

    this.selectedFileName.set('');

    this.selectedFile.set(null);

    this.fileError.set('');

    this.submitError.set('');

    this.submitSuccess.set(false);


    if (!file) {
      return;
    }


    const maximumFileSize =
      5 * 1024 * 1024;


    const isPdf =
      file.type === 'application/pdf' ||
      file.name
        .toLowerCase()
        .endsWith('.pdf');


    if (!isPdf) {

      this.fileError.set(
        'Only PDF files are allowed.'
      );

      input.value = '';

      return;
    }


    if (
      file.size >
      maximumFileSize
    ) {

      this.fileError.set(
        'The PDF file must be smaller than 5 MB.'
      );

      input.value = '';

      return;
    }


    this.selectedFile.set(
      file
    );

    this.selectedFileName.set(
      file.name
    );
  }


  removeSelectedFile(
    input: HTMLInputElement
  ): void {

    input.value = '';

    this.clearSelectedFile();
  }


  private clearSelectedFile(): void {

    this.selectedFile.set(null);

    this.selectedFileName.set('');

    this.fileError.set('');
  }


  /* =========================
     SUBMIT APPLICATION
  ========================= */

  submitApplication(
    form: HTMLFormElement,
    event: Event
  ): void {

    event.preventDefault();

    const offer =
      this.selectedOffer();

    const cv =
      this.selectedFile();


    if (!offer) {

      this.submitError.set(
        'No internship offer selected.'
      );

      return;
    }


    if (!this.isAvailable(offer)) {

      this.submitError.set(
        'This internship offer is no longer available.'
      );

      return;
    }


    if (!form.checkValidity()) {

      form.reportValidity();

      return;
    }


    if (!cv) {

      this.fileError.set(
        'Please upload your CV.'
      );

      return;
    }


    const formValues =
      new FormData(form);


    const firstName =
      formValues
        .get('firstName')
        ?.toString()
        .trim() ?? '';


    const lastName =
      formValues
        .get('lastName')
        ?.toString()
        .trim() ?? '';


    const email =
      formValues
        .get('email')
        ?.toString()
        .trim() ?? '';


    const phone =
      formValues
        .get('phone')
        ?.toString()
        .trim() ?? '';


    const university =
      formValues
        .get('university')
        ?.toString()
        .trim() ?? '';


    const major =
      formValues
        .get('major')
        ?.toString()
        .trim() ?? '';


    const message =
      formValues
        .get('message')
        ?.toString()
        .trim() ?? '';


    this.submitting.set(true);

    this.submitSuccess.set(false);

    this.submitError.set('');


    this.internshipApplicationService
      .submitApplication(
        offer.id,
        firstName,
        lastName,
        email,
        phone,
        university,
        major,
        message,
        cv
      )
      .subscribe({

        next: (applicationId) => {

          console.log(
            'Application created:',
            applicationId
          );

          this.submitting.set(false);

          this.submitSuccess.set(true);

          this.submitError.set('');

          form.reset();

          this.clearSelectedFile();

          /*
           * Backend increments currentCandidates.
           * Reload offers to obtain the new value/status.
           */
          this.loadOffers();
        },


        error: (error) => {

          console.error(
            'Application submit error:',
            error
          );

          this.submitting.set(false);

          this.submitSuccess.set(false);


          if (
            typeof error?.error ===
            'string'
          ) {

            this.submitError.set(
              error.error
            );

          } else {

            this.submitError.set(
              'Could not submit your application. Please try again.'
            );
          }
        }

      });
  }


  /* =========================
     RESET MESSAGES
  ========================= */

  private resetApplicationMessages(): void {

    this.submitSuccess.set(false);

    this.submitError.set('');

    this.fileError.set('');
  }
}