import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  currentLanguage = 'en';

  constructor(private translate: TranslateService) {
    const savedLanguage = localStorage.getItem('language');

    const initialLanguage =
      savedLanguage === 'ar' || savedLanguage === 'en'
        ? savedLanguage
        : 'en';

    this.translate.setFallbackLang('en');
    this.setLanguage(initialLanguage);
  }

  setLanguage(language: 'en' | 'ar'): void {
    this.currentLanguage = language;

    this.translate.use(language);

    localStorage.setItem('language', language);

    document.documentElement.lang = language;
    document.documentElement.dir =
      language === 'ar' ? 'rtl' : 'ltr';
  }

  toggleLanguage(): void {
    const nextLanguage =
      this.currentLanguage === 'en' ? 'ar' : 'en';

    this.setLanguage(nextLanguage);
  }
}