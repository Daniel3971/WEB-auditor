import { Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-careers',
  imports: [
    RouterLink,
    TranslatePipe
  ],
  templateUrl: './careers.html',
  styleUrl: './careers.css'
})
export class Careers {
  selectedFileName = signal('');
  fileError = signal('');

  constructor(private translate: TranslateService) {}

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
      this.fileError.set(
        this.translate.instant('CAREERS.FORM.CV.PDF_ONLY_ERROR')
      );

      input.value = '';
      return;
    }

    if (file.size > maximumFileSize) {
      this.fileError.set(
        this.translate.instant('CAREERS.FORM.CV.SIZE_ERROR')
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