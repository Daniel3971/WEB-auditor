import { Routes } from '@angular/router';

import { Home } from './pages/home/home';
import { About } from './pages/about/about';
import { Services } from './pages/services/services';
import { Locations } from './pages/locations/locations';
import { OfficialForms } from './pages/official-forms/official-forms';
import { Careers } from './pages/careers/careers';
import { InternshipOffers } from './pages/internship-offers/internship-offers';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'about', component: About },
  { path: 'services', component: Services },
  { path: 'locations', component: Locations },
  { path: 'official-forms', component: OfficialForms },
  { path: 'careers', component: Careers },
  {
    path: 'internship-offers',
    component: InternshipOffers
  },

  // The wildcard route must always be last.
  { path: '**', redirectTo: '' }
];