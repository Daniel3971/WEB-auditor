import {
  inject
} from '@angular/core';

import {
  CanActivateFn,
  Router
} from '@angular/router';

import {
  catchError,
  map,
  of
} from 'rxjs';

import {
  AdminAuthService
} from '../services/admin-auth.service';


export const adminAuthGuard:
  CanActivateFn = () => {

  const authService =
    inject(
      AdminAuthService
    );

  const router =
    inject(
      Router
    );


  return authService
    .getCurrentAdmin()
    .pipe(

      map(
        admin => {

          if (
            admin.authenticated
          ) {

            return true;
          }


          return router.createUrlTree([
            '/admin/login'
          ]);
        }
      ),


      catchError(
        () => {

          return of(
            router.createUrlTree([
              '/admin/login'
            ])
          );
        }
      )

    );
};