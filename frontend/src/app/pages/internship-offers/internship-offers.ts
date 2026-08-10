import { Component, signal } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-internship-offers',
  imports: [
    TranslatePipe
  ],
  templateUrl: './internship-offers.html',
  styleUrl: './internship-offers.css'
})
export class InternshipOffers {
  showApplicationForm = signal(false);
  selectedFileName = signal('');
  fileError = signal('');

  openApplicationForm(): void {
    this.showApplicationForm.set(true);

    setTimeout(() => {
      document
        .getElementById('internship-application')
        ?.scrollIntoView({
          behavior: 'smooth',
          block: 'start'
        });
    });
  }

  closeApplicationForm(): void {
    this.showApplicationForm.set(false);
    this.selectedFileName.set('');
    this.fileError.set('');
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    this.selectedFileName.set('');
    this.fileError.set('');

    if (!file) {
      return;
    }

    const maximumFileSize = 5 * 1024 * 1024;

    const isPdf =
      file.type === 'application/pdf' ||
      file.name.toLowerCase().endsWith('.pdf');

    if (!isPdf) {
      this.fileError.set('Only PDF files are allowed.');
      input.value = '';
      return;
    }

    if (file.size > maximumFileSize) {
      this.fileError.set(
        'The PDF file must be smaller than 5 MB.'
      );

      input.value = '';
      return;
    }

    this.selectedFileName.set(file.name);
  }

  removeSelectedFile(input: HTMLInputElement): void {
    input.value = '';
    this.selectedFileName.set('');
    this.fileError.set('');
  }
}