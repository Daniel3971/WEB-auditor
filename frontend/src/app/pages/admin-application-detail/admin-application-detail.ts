import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AdminInternshipApplication, ApplicationStatus } from '../../models/admin-internship-application';
import { AdminInternshipApplicationService } from '../../services/admin-internship-application.service';
import { AdminAuthService } from '../../services/admin-auth.service';

@Component({
  selector: 'app-admin-application-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-application-detail.html',
  styleUrl: './admin-application-detail.css'
})
export class AdminApplicationDetail implements OnInit {
  application = signal<AdminInternshipApplication | null>(null);
  loading = signal(true);
  savingStatus = signal(false);
  downloadingCv = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private applicationService: AdminInternshipApplicationService,
    private adminAuthService: AdminAuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isInteger(id) || id < 1) {
      this.errorMessage.set('Invalid application number.');
      this.loading.set(false);
      return;
    }
    this.loadApplication(id);
  }

  loadApplication(id: number): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.applicationService.getApplication(id).subscribe({
      next: application => {
        this.application.set(application);
        this.loading.set(false);
      },
      error: error => this.handleRequestError(error, 'Could not load this application.')
    });
  }

  changeStatus(event: Event): void {
    const application = this.application();
    if (!application) return;

    const select = event.target as HTMLSelectElement;
    const previousStatus = application.applicationStatus;
    const nextStatus = select.value as ApplicationStatus;
    if (nextStatus === previousStatus) return;

    this.savingStatus.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.applicationService.updateStatus(application.id, nextStatus).subscribe({
      next: updated => {
        this.application.set(updated);
        this.savingStatus.set(false);
        this.successMessage.set(`Status changed to ${updated.applicationStatus}.`);
      },
      error: error => {
        select.value = previousStatus;
        this.savingStatus.set(false);
        this.handleRequestError(error, 'Could not change the application status.');
      }
    });
  }

  openCv(): void { this.fetchCv(true); }
  downloadCv(): void { this.fetchCv(false); }

  private fetchCv(openInNewTab: boolean): void {
    const application = this.application();
    if (!application || this.downloadingCv()) return;

    const targetWindow = openInNewTab ? window.open('', '_blank') : null;
    this.downloadingCv.set(true);
    this.errorMessage.set('');

    this.applicationService.downloadCv(application.id).subscribe({
      next: blob => {
        const url = URL.createObjectURL(blob);
        if (openInNewTab && targetWindow) {
          targetWindow.location.href = url;
        } else {
          const link = document.createElement('a');
          link.href = url;
          link.download = application.cvFileName || 'cv.pdf';
          link.click();
        }
        window.setTimeout(() => URL.revokeObjectURL(url), 60_000);
        this.downloadingCv.set(false);
      },
      error: error => {
        targetWindow?.close();
        this.downloadingCv.set(false);
        this.handleRequestError(error, 'Could not retrieve the CV.');
      }
    });
  }

  private handleRequestError(error: { status?: number }, message: string): void {
    this.loading.set(false);
    if (error.status === 401 || error.status === 403) {
      this.router.navigate(['/admin/login']);
      return;
    }
    this.errorMessage.set(error.status === 404 ? 'Application or CV not found.' : message);
  }

  logout(): void {
    this.adminAuthService.logout().subscribe({
      next: () => this.router.navigate(['/admin/login']),
      error: () => this.router.navigate(['/admin/login'])
    });
  }
}
