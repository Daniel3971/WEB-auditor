import { Component, signal } from '@angular/core';
import {
  RouterLink,
  RouterLinkActive
} from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

import { LanguageService } from '../../services/language.service';

@Component({
  selector: 'app-navbar',
  imports: [
    RouterLink,
    RouterLinkActive,
    TranslatePipe
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {
  readonly menuOpen = signal(false);

  constructor(
    public language: LanguageService
  ) {}

  toggleMenu(): void {
    this.menuOpen.update((isOpen) => !isOpen);
  }

  closeMenu(): void {
    this.menuOpen.set(false);
  }
}
