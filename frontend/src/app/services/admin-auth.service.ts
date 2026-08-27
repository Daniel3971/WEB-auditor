import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs';
import { tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { CsrfTokenService } from './csrf-token.service';


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
    `${environment.apiUrl}/api/auth`;


  constructor(
    private http: HttpClient,
    private csrfTokenService: CsrfTokenService
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


    return this.initializeCsrf().pipe(
      switchMap(() =>
        this.http.post<AdminUser>(
          `${this.apiUrl}/login`,
          body,
          { withCredentials: true }
        )
      )
    );
  }

  initializeCsrf(): Observable<{ token: string; headerName: string }> {
    return this.http.get<{ token: string; headerName: string }>(
      `${this.apiUrl}/csrf`,
      { withCredentials: true }
    ).pipe(
      tap(response => this.csrfTokenService.setToken(response.token))
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
    ).pipe(
      tap(() => this.csrfTokenService.clearToken())
    );
  }
}
