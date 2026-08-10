import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { InternshipOffer } from '../models/internship-offer';

@Injectable({
  providedIn: 'root'
})
export class InternshipOfferService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl =
    'http://localhost:8080/api/internship-offers';

  getOffers(): Observable<InternshipOffer[]> {
    return this.http.get<InternshipOffer[]>(this.apiUrl);
  }

  getOfferById(id: number): Observable<InternshipOffer> {
    return this.http.get<InternshipOffer>(
      `${this.apiUrl}/${id}`
    );
  }
}