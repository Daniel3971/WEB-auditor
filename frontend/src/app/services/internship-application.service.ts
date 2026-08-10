import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InternshipApplicationService {

  private readonly apiUrl =
    'http://localhost:8080/api/internship-applications';

  constructor(
    private http: HttpClient
  ) {}

  submitApplication(
    offerId: number,
    firstName: string,
    lastName: string,
    email: string,
    phone: string,
    university: string,
    major: string,
    message: string,
    cv: File
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

    return this.http.post<number>(
      this.apiUrl,
      formData
    );
  }
}