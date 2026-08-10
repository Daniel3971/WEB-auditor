import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { InternshipOffer } from '../models/internship-offer';

@Injectable({
  providedIn: 'root'
})
export class InternshipOfferService {

  private readonly apiUrl =
    'http://localhost:8080/api/internship-offers';

  constructor(private http: HttpClient) {}

  getAllOffers(): Observable<InternshipOffer[]> {
    return this.http.get<InternshipOffer[]>(this.apiUrl);
  }
}