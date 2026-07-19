import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-locations',
  imports: [
    RouterLink,
    TranslatePipe
  ],
  templateUrl: './locations.html',
  styleUrl: './locations.css'
})
export class Locations {}