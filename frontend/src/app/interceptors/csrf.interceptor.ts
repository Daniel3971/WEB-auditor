import { HttpInterceptorFn } from '@angular/common/http';

const SAFE_METHODS = new Set(['GET', 'HEAD', 'OPTIONS']);

export const csrfInterceptor: HttpInterceptorFn = (request, next) => {
  if (
    SAFE_METHODS.has(request.method.toUpperCase()) ||
    !request.url.startsWith('/api/')
  ) {
    return next(request);
  }

  const token = readCookie('XSRF-TOKEN');

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
