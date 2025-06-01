import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TokenService } from '../../service/token/token.service';

@Component({
  selector: 'app-navbar',
  imports: [],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  menuActive = false;

  constructor(private router: Router, private tokenService: TokenService) {
  }

  get isLoggedIn(): boolean {
    return !!this.tokenService.getToken();
  }

  navigate(route: string){
    console.log("navigating to " + route)
    this.router.navigate([route]);
  }

  toggleMenu() {
    this.menuActive = !this.menuActive;
  }

  closeMenu() {
    this.menuActive = false;
  }

  logout() {
    this.tokenService.clearToken();
    this.closeMenu();
    this.router.navigate(['/login']);
  }
}
