import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { CompanyService } from '../models/company-service';

@Injectable({
  providedIn: 'root'
})
export class CompanyServiceApi {
  private readonly apiUrl = 'http://localhost:8080/api/services';

  constructor(private http: HttpClient) {}

  getAllServices(): Observable<CompanyService[]> {
    return this.http.get<CompanyService[]>(this.apiUrl);
  }
}