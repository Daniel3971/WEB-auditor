import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InternshipApplicationService {

  private readonly apiUrl =
    '/api/internship-applications';

  constructor(
    private http: HttpClient
  ) {}

  getTurnstileConfig(): Observable<TurnstileConfig> {
    return this.http.get<TurnstileConfig>('/api/turnstile/config');
  }

  submitApplication(
    offerId: number,
    firstName: string,
    lastName: string,
    email: string,
    phone: string,
    university: string,
    major: string,
    message: string,
    cv: File,
    turnstileToken: string
  ): Observable<number> {

    const formData = new FormData();

    formData.append(
      'offerId',
      offerId.toString()
    );

    formData.append(
      'firstName',
      firstName
    );

    formData.append(
      'lastName',
      lastName
    );

    formData.append(
      'email',
      email
    );

    formData.append(
      'phone',
      phone
    );

    formData.append(
      'university',
      university
    );

    formData.append(
      'major',
      major
    );

    formData.append(
      'message',
      message
    );

    formData.append(
      'cv',
      cv
    );

    if (turnstileToken) {
      formData.append('turnstileToken', turnstileToken);
    }

    return this.http.post<number>(
      this.apiUrl,
      formData
    );
  }
}

export interface TurnstileConfig {
  enabled: boolean;
  siteKey: string;
}
