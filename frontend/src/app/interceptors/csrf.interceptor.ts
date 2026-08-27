import { inject } from '@angular/core';
import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { CsrfTokenService } from '../services/csrf-token.service';

const SAFE_METHODS = new Set(['GET', 'HEAD', 'OPTIONS']);

export const csrfInterceptor: HttpInterceptorFn = (request, next) => {
  const csrfTokenService = inject(CsrfTokenService);

  if (
    SAFE_METHODS.has(request.method.toUpperCase()) ||
    !request.url.startsWith(`${environment.apiUrl}/api/`)
  ) {
    return next(request);
  }

  const token = csrfTokenService.getToken() ?? readCookie('XSRF-TOKEN');

  if (!token) {
    return next(request);
  }

  return next(
    request.clone({
      setHeaders: { 'X-XSRF-TOKEN': token },
      withCredentials: true
    })
  );
};

function readCookie(name: string): string | null {
  const prefix = `${name}=`;
  const cookie = document.cookie
    .split(';')
    .map(value => value.trim())
    .find(value => value.startsWith(prefix));

  return cookie ? decodeURIComponent(cookie.substring(prefix.length)) : null;
}
