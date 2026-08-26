import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AdminInternshipApplication, ApplicationStatus } from '../../models/admin-internship-application';
import { AdminInternshipApplicationService } from '../../services/admin-internship-application.service';
import { AdminAuthService } from '../../services/admin-auth.service';

interface OfferFilterOption { id: number; title: string; }

@Component({
  selector: 'app-admin-applications',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-applications.html',
  styleUrl: './admin-applications.css'
})
export class AdminApplications implements OnInit {
  applications = signal<AdminInternshipApplication[]>([]);
  loading = signal(true);
  errorMessage = signal('');
  searchTerm = signal('');
  offerFilter = signal('ALL');
  statusFilter = signal<'ALL' | ApplicationStatus>('ALL');

  constructor(
    private applicationService: AdminInternshipApplicationService,
    private adminAuthService: AdminAuthService,
    private router: Router
  ) {}

  ngOnInit(): void { this.loadApplications(); }

  loadApplications(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.applicationService.getAllApplications().subscribe({
      next: applications => {
        this.applications.set(applications);
        this.loading.set(false);
      },
      error: error => {
        this.loading.set(false);
        if (error.status === 401 || error.status === 403) {
          this.router.navigate(['/admin/login']);
          return;
        }
        this.errorMessage.set('Could not load applications. Please try again.');
      }
    });
  }

  get offerOptions(): OfferFilterOption[] {
    const offers = new Map<number, string>();
    for (const application of this.applications()) {
      offers.set(application.offerId, application.offerTitleEn);
    }
    return Array.from(offers, ([id, title]) => ({ id, title }))
      .sort((a, b) => a.title.localeCompare(b.title));
  }

  get filteredApplications(): AdminInternshipApplication[] {
    const search = this.searchTerm().trim().toLowerCase();
    const offerId = this.offerFilter();
    const status = this.statusFilter();

    return this.applications().filter(application => {
      const searchableText = [
        application.firstName, application.lastName, application.email,
        application.phone, application.university ?? '', application.major ?? '',
        application.offerTitleEn, application.offerTitleAr
      ].join(' ').toLowerCase();

      return (!search || searchableText.includes(search)) &&
        (offerId === 'ALL' || application.offerId === Number(offerId)) &&
        (status === 'ALL' || application.applicationStatus === status);
    });
  }

  updateSearch(event: Event): void {
    this.searchTerm.set((event.target as HTMLInputElement).value);
  }

  updateOfferFilter(event: Event): void {
    this.offerFilter.set((event.target as HTMLSelectElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as 'ALL' | ApplicationStatus);
  }

  clearFilters(): void {
    this.searchTerm.set('');
    this.offerFilter.set('ALL');
    this.statusFilter.set('ALL');
  }

  logout(): void {
    this.adminAuthService.logout().subscribe({
      next: () => this.router.navigate(['/admin/login']),
      error: () => this.router.navigate(['/admin/login'])
    });
  }
}
