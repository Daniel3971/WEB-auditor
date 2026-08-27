import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminInternshipApplication, ApplicationStatus } from '../models/admin-internship-application';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AdminInternshipApplicationService {
  private readonly apiUrl =
    `${environment.apiUrl}/api/admin/internship-applications`;

  constructor(private http: HttpClient) {}

  getAllApplications(): Observable<AdminInternshipApplication[]> {
    return this.http.get<AdminInternshipApplication[]>(
      this.apiUrl,
      { withCredentials: true }
    );
  }

  getApplicationCount(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(
      `${this.apiUrl}/count`,
      { withCredentials: true }
    );
  }

  getApplication(id: number): Observable<AdminInternshipApplication> {
    return this.http.get<AdminInternshipApplication>(
      `${this.apiUrl}/${id}`,
      { withCredentials: true }
    );
  }

  updateStatus(
    id: number,
    status: ApplicationStatus
  ): Observable<AdminInternshipApplication> {
    return this.http.patch<AdminInternshipApplication>(
      `${this.apiUrl}/${id}/status`,
      { status },
      { withCredentials: true }
    );
  }

  downloadCv(id: number): Observable<Blob> {
    return this.http.get(
      `${this.apiUrl}/${id}/cv`,
      { withCredentials: true, responseType: 'blob' }
    );
  }
}
