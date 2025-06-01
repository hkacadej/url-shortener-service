import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { authGuard } from './guards/auth.guard';
import { ShortUrlComponent } from './components/short-url/short-url.component';
import { UrlListComponent } from './components/url-list/url-list.component';
import { OriginalUrlComponent } from './components/original-url/original-url.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'redirect',
    component: OriginalUrlComponent,
    canActivate: [authGuard]
  },
  {
    path: 'shorten',
    component: ShortUrlComponent,
    canActivate: [authGuard]
  },
  {
    path: 'urls',
    component: UrlListComponent,
    canActivate: [authGuard]
  },
  { path: '', redirectTo: 'urls', pathMatch: 'full' },
  { path: '**', redirectTo: 'urls' }
];
