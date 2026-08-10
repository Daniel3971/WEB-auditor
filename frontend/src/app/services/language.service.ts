import { Injectable, inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

export type AppLanguage = 'en' | 'ar';

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  private readonly translate = inject(TranslateService);

  currentLanguage: AppLanguage = 'en';

  constructor() {
    const savedLanguage = localStorage.getItem('language');

    const initialLanguage: AppLanguage =
      savedLanguage === 'ar' ? 'ar' : 'en';

    this.changeLanguage(initialLanguage);
  }

  changeLanguage(language: AppLanguage): void {
    this.currentLanguage = language;

    this.translate.use(language);

    localStorage.setItem('language', language);

    document.documentElement.lang = language;
    document.documentElement.dir =
      language === 'ar' ? 'rtl' : 'ltr';
  }
}