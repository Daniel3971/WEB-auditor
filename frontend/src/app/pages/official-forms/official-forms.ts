import {
  Component,
  computed,
  inject,
  signal
} from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { TranslatePipe } from '@ngx-translate/core';

import { LanguageService } from '../../services/language.service';

interface OfficialForm {
  id: number;

  titleEn: string;
  titleAr: string;

  descriptionEn: string;
  descriptionAr: string;

  category: string;

  fileName: string;
  fileSize: string;
  updatedAt: string;
  downloadUrl: string;
}

interface FormCategory {
  value: string;
  labelKey: string;
}

@Component({
  selector: 'app-official-forms',
  imports: [
    TranslatePipe
  ],
  templateUrl: './official-forms.html',
  styleUrl: './official-forms.css'
})
export class OfficialForms {
  private readonly http = inject(HttpClient);

  readonly language = inject(LanguageService);

  readonly searchTerm = signal('');
  readonly selectedCategory = signal('All');

  readonly forms = signal<OfficialForm[]>([]);
  readonly loading = signal(true);
  readonly loadError = signal('');

  readonly categories: FormCategory[] = [
    {
      value: 'All',
      labelKey: 'OFFICIAL_FORMS.CATEGORIES.ALL'
    },
    {
      value: 'Betterment-Tax',
      labelKey: 'OFFICIAL_FORMS.CATEGORIES.BETTERMENT_TAX'
    },
    {
      value: 'Built-Property-Tax',
      labelKey: 'OFFICIAL_FORMS.CATEGORIES.BUILT_PROPERTY_TAX'
    },
    {
      value: 'Commercial-Industrial-and-Non-Commercial-Profits-Tax',
      labelKey: 'OFFICIAL_FORMS.CATEGORIES.COMMERCIAL_PROFITS_TAX'
    },
    {
      value: 'Indirect-Taxes',
      labelKey: 'OFFICIAL_FORMS.CATEGORIES.INDIRECT_TAXES'
    },
    {
      value: 'Payroll-and-Wage-Tax',
      labelKey: 'OFFICIAL_FORMS.CATEGORIES.PAYROLL_TAX'
    },
    {
      value: 'Tax-on-Income-from-Movable-Capital',
      labelKey: 'OFFICIAL_FORMS.CATEGORIES.MOVABLE_CAPITAL_TAX'
    },
    {
      value: 'Transfer-duty',
      labelKey: 'OFFICIAL_FORMS.CATEGORIES.TRANSFER_DUTY'
    },
    {
      value: 'TVA',
      labelKey: 'OFFICIAL_FORMS.CATEGORIES.TVA'
    },
    {
      value: 'VAT',
      labelKey: 'OFFICIAL_FORMS.CATEGORIES.VAT'
    }
  ];

  readonly filteredForms = computed(() => {
    const search = this.searchTerm().trim().toLowerCase();
    const selectedCategory = this.selectedCategory();

    return this.forms().filter((form) => {
      const matchesCategory =
        selectedCategory === 'All' ||
        form.category === selectedCategory;

      /*
       * Search both English and Arabic content.
       * This means search continues working after switching languages.
       */
      const searchableText = [
        form.titleEn,
        form.titleAr,
        form.descriptionEn,
        form.descriptionAr,
        form.category,
        form.fileName
      ]
        .join(' ')
        .toLowerCase();

      const matchesSearch =
        search.length === 0 ||
        searchableText.includes(search);

      return matchesCategory && matchesSearch;
    });
  });

  constructor() {
    this.loadForms();
  }

  getTitle(form: OfficialForm): string {
    return this.language.currentLanguage === 'ar'
      ? form.titleAr
      : form.titleEn;
  }

  getDescription(form: OfficialForm): string {
    return this.language.currentLanguage === 'ar'
      ? form.descriptionAr
      : form.descriptionEn;
  }

  getCategoryLabelKey(category: string): string {
    const matchingCategory = this.categories.find(
      (item) => item.value === category
    );

    return matchingCategory?.labelKey ??
      'OFFICIAL_FORMS.CATEGORIES.OTHER';
  }

  updateSearch(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchTerm.set(input.value);
  }

  selectCategory(category: string): void {
    this.selectedCategory.set(category);
  }

  private loadForms(): void {
    this.loading.set(true);
    this.loadError.set('');

    this.http
      .get<OfficialForm[]>('/data/official-forms.json')
      .subscribe({
        next: (forms) => {
          this.forms.set(forms);
          this.loading.set(false);
        },

        error: (error) => {
          console.error(
            'Could not load official forms:',
            error
          );

          this.forms.set([]);
          this.loading.set(false);

          this.loadError.set(
            'The official forms could not be loaded.'
          );
        }
      });
  }
}