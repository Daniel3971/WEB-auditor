import { Component, OnInit, signal } from '@angular/core';

import { CompanyService } from './models/company-service';
import { CompanyServiceApi } from './services/company-service-api';

@Component({
  selector: 'app-root',
  imports: [],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  services = signal<CompanyService[]>([]);
  loading = signal(true);
  errorMessage = signal('');

  constructor(private companyServiceApi: CompanyServiceApi) {}

  ngOnInit(): void {
    this.loadServices();
  }

  loadServices(): void {
    this.companyServiceApi.getAllServices().subscribe({
      next: (services) => {
        this.services.set(services);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Could not load services:', error);
        this.errorMessage.set('Could not load services.');
        this.loading.set(false);
      }
    });
  }
}