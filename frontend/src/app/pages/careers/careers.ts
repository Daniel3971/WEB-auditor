import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-careers',
  imports: [],
  templateUrl: './careers.html',
  styleUrl: './careers.css'
})
export class Careers {
  selectedFileName = signal('');
  fileError = signal('');

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
      this.fileError.set('The PDF file must be smaller than 5 MB.');
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