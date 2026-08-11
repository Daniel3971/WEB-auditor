import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


export interface AdminLoginRequest {
  username: string;
  password: string;
}


export interface AdminUser {
  authenticated: boolean;
  username?: string;
  roles?: unknown[];
}


@Injectable({
  providedIn: 'root'
})
export class AdminAuthService {

  private readonly apiUrl =
    'http://localhost:8080/api/auth';


  constructor(
    private http: HttpClient
  ) {}


  /* =========================
     LOGIN
  ========================= */

  login(
    username: string,
    password: string
  ): Observable<AdminUser> {

    const body: AdminLoginRequest = {
      username,
      password
    };


    return this.http.post<AdminUser>(
      `${this.apiUrl}/login`,
      body,
      {
        withCredentials: true
      }
    );
  }


  /* =========================
     CURRENT ADMIN
  ========================= */

  getCurrentAdmin():
    Observable<AdminUser> {

    return this.http.get<AdminUser>(
      `${this.apiUrl}/me`,
      {
        withCredentials: true
      }
    );
  }


  /* =========================
     LOGOUT
  ========================= */

  logout():
    Observable<unknown> {

    return this.http.post(
      `${this.apiUrl}/logout`,
      {},
      {
        withCredentials: true
      }
    );
  }
}