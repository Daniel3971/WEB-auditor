import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { CompanyService } from '../models/company-service';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CompanyServiceApi {
  private readonly apiUrl = `${environment.apiUrl}/api/services`;

  constructor(private http: HttpClient) {}

  getAllServices(): Observable<CompanyService[]> {
    return this.http.get<CompanyService[]>(this.apiUrl);
  }
}
