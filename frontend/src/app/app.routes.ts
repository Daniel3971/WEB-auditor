import { Routes } from '@angular/router';

import { Home } from './pages/home/home';
import { About } from './pages/about/about';
import { Services } from './pages/services/services';
import { Locations } from './pages/locations/locations';
import { OfficialForms } from './pages/official-forms/official-forms';
import { Careers } from './pages/careers/careers';
import { Contact } from './pages/contact/contact';
import { InternshipOffers } from './pages/internship-offers/internship-offers';
import { AdminLogin } from './pages/admin-login/admin-login';
import { AdminDashboard } from './pages/admin-dashboard/admin-dashboard';
import { adminAuthGuard } from './guards/admin-auth.guard';
import {AdminCreateOffer} from './pages/admin-create-offer/admin-create-offer';
import {AdminOffers} from './pages/admin-offers/admin-offers';
import {AdminEditOffer} from './pages/admin-edit-offer/admin-edit-offer';
import { AdminApplications } from './pages/admin-applications/admin-applications';
import { AdminApplicationDetail } from './pages/admin-application-detail/admin-application-detail';
export const routes: Routes = [
  { path: '', component: Home },
  { path: 'about', component: About },
  { path: 'services', component: Services },
  { path: 'locations', component: Locations },
  { path: 'official-forms', component: OfficialForms },
  { path: 'careers', component: Careers },
  { path: 'contact', component: Contact },
  {path: 'admin/login',component: AdminLogin},
  {path: 'admin/dashboard',component: AdminDashboard,canActivate: [adminAuthGuard]},

  { path: 'internship-offers', component: InternshipOffers },
  {path: 'admin/offers',component: AdminOffers,canActivate: [adminAuthGuard]},
  {path: 'admin/offers/new',component: AdminCreateOffer,canActivate: [adminAuthGuard]},
  {path: 'admin/offers/:id/edit',component: AdminEditOffer,canActivate: [adminAuthGuard]},
  {path: 'admin/applications',component: AdminApplications,canActivate: [adminAuthGuard]},
  {path: 'admin/applications/:id',component: AdminApplicationDetail,canActivate: [adminAuthGuard]},
  { path: '**', redirectTo: '' }
];
