import { Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

interface OfficialForm {
  id: number;
  title: string;
  description: string;
  category: 'Tax' | 'Company' | 'Accounting' | 'Other';
  fileName: string;
  fileSize: string;
  updatedAt: string;
  downloadUrl: string;
}

@Component({
  selector: 'app-official-forms',
  imports: [
    RouterLink,
    TranslatePipe
  ],
  templateUrl: './official-forms.html',
  styleUrl: './official-forms.css'
})
export class OfficialForms {
  readonly categories = [
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
      value: 'VAT',
      labelKey: 'OFFICIAL_FORMS.CATEGORIES.VAT'
    }
  ];

  searchTerm = signal('');
  selectedCategory = signal('All');

  forms = signal<OfficialForm[]>([
    {
      id: 1,
      title: 'Tax Declaration Form',
      description:
        'Official form used for preparing and submitting annual tax declarations.',
      category: 'Tax',
      fileName: 'tax-declaration-form.pdf',
      fileSize: '420 KB',
      updatedAt: 'July 2026',
      downloadUrl: '/sample-pdfs/tax-declaration-form.pdf'
    },
    {
      id: 2,
      title: 'Company Registration Form',
      description:
        'Required document for company creation and registration procedures.',
      category: 'Company',
      fileName: 'company-registration-form.pdf',
      fileSize: '610 KB',
      updatedAt: 'June 2026',
      downloadUrl: '/sample-pdfs/company-registration-form.pdf'
    },
    {
      id: 3,
      title: 'Accounting Documents Checklist',
      description:
        'Checklist of documents required for bookkeeping and financial reporting.',
      category: 'Accounting',
      fileName: 'accounting-checklist.pdf',
      fileSize: '285 KB',
      updatedAt: 'May 2026',
      downloadUrl: '/sample-pdfs/accounting-checklist.pdf'
    },
    {
      id: 4,
      title: 'Audit Preparation Checklist',
      description:
        'A practical checklist to help companies prepare for an audit engagement.',
      category: 'Accounting',
      fileName: 'audit-preparation-checklist.pdf',
      fileSize: '360 KB',
      updatedAt: 'April 2026',
      downloadUrl: '/sample-pdfs/audit-preparation-checklist.pdf'
    },
    {
      id: 5,
      title: 'General Client Information Form',
      description:
        'General company and contact information form for new clients.',
      category: 'Other',
      fileName: 'client-information-form.pdf',
      fileSize: '190 KB',
      updatedAt: 'March 2026',
      downloadUrl: '/sample-pdfs/client-information-form.pdf'
    }
  ]);
}