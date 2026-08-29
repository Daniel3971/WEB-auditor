import { Component } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-locations',
  imports: [
    TranslatePipe
  ],
  templateUrl: './locations.html',
  styleUrl: './locations.css'
})
export class Locations {}
