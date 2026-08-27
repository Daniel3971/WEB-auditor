import {
  Injectable
} from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import {
  Observable
} from 'rxjs';

import {
  InternshipOffer
} from '../models/internship-offer';
import { environment } from '../../environments/environment';


export interface AdminInternshipOfferRequest {

  titleEn: string;

  titleAr: string;

  descriptionEn: string;

  descriptionAr: string;

  missionEn: string;

  missionAr: string;

  profileEn: string;

  profileAr: string;

  duration: string;

  applicationDeadline: string;

  maxCandidates: number;

  status: 'OPEN' | 'CLOSED';
}


@Injectable({
  providedIn: 'root'
})
export class AdminInternshipOfferService {

  private readonly apiUrl =
    `${environment.apiUrl}/api/admin/internship-offers`;


  constructor(
    private http: HttpClient
  ) {}


  getAllOffers():
    Observable<InternshipOffer[]> {

    return this.http.get<InternshipOffer[]>(
      this.apiUrl,
      {
        withCredentials: true
      }
    );
  }


  getOffer(
    id: number
  ): Observable<InternshipOffer> {

    return this.http.get<InternshipOffer>(
      `${this.apiUrl}/${id}`,
      {
        withCredentials: true
      }
    );
  }


  createOffer(
    offer: AdminInternshipOfferRequest
  ): Observable<InternshipOffer> {

    return this.http.post<InternshipOffer>(
      this.apiUrl,
      offer,
      {
        withCredentials: true
      }
    );
  }


  updateOffer(
    id: number,
    offer: AdminInternshipOfferRequest
  ): Observable<InternshipOffer> {

    return this.http.put<InternshipOffer>(
      `${this.apiUrl}/${id}`,
      offer,
      {
        withCredentials: true
      }
    );
  }


  changeStatus(
    id: number,
    status: 'OPEN' | 'CLOSED'
  ): Observable<InternshipOffer> {

    return this.http.patch<InternshipOffer>(
      `${this.apiUrl}/${id}/status`,
      {
        status
      },
      {
        withCredentials: true
      }
    );
  }

  getArchivedOffers(): Observable<InternshipOffer[]> {
    return this.http.get<InternshipOffer[]>(
      `${this.apiUrl}/archived`,
      { withCredentials: true }
    );
  }

  removeOffer(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(
      `${this.apiUrl}/${id}`,
      { withCredentials: true }
    );
  }

  restoreOffer(id: number): Observable<InternshipOffer> {
    return this.http.patch<InternshipOffer>(
      `${this.apiUrl}/${id}/restore`,
      {},
      { withCredentials: true }
    );
  }

  permanentlyDeleteOffer(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(
      `${this.apiUrl}/${id}/permanent`,
      { withCredentials: true }
    );
  }
}
