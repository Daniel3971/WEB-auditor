import {
  Component,
  signal
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  Router
} from '@angular/router';

import {
  AdminAuthService
} from '../../services/admin-auth.service';


@Component({
  selector: 'app-admin-login',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule
  ],

  templateUrl:
    './admin-login.html',

  styleUrl:
    './admin-login.css'
})
export class AdminLogin {

  username = '';

  password = '';


  loading =
    signal(false);

  errorMessage =
    signal('');

  showPassword =
    signal(false);


  constructor(
    private adminAuthService:
      AdminAuthService,

    private router:
      Router
  ) {}


  /* =========================
     LOGIN
  ========================= */

  login(): void {

    this.errorMessage.set('');


    const username =
      this.username.trim();


    if (
      !username ||
      !this.password
    ) {

      this.errorMessage.set(
        'Please enter your username and password.'
      );

      return;
    }


    this.loading.set(true);


    this.adminAuthService
      .login(
        username,
        this.password
      )
      .subscribe({

        next: () => {

          this.loading.set(false);

          this.router.navigate([
            '/admin/dashboard'
          ]);
        },


        error: (error) => {

          console.error(
            'Admin login failed:',
            error
          );


          this.loading.set(false);


          if (
            error.status === 401 ||
            error.status === 403
          ) {

            this.errorMessage.set(
              'Incorrect username or password.'
            );

            return;
          }


          this.errorMessage.set(
            'Unable to connect to the server. Please try again.'
          );
        }

      });
  }


  /* =========================
     PASSWORD VISIBILITY
  ========================= */

  togglePassword(): void {

    this.showPassword.update(
      value => !value
    );
  }
}