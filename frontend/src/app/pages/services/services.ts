import { Component } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-services',
  imports: [
    TranslatePipe
  ],
  templateUrl: './services.html',
  styleUrl: './services.css'
})
export class Services {}