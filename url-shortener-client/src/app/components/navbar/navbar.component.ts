import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  menuActive = false;

  constructor(private router: Router) {
  }

  get isLoggedIn(): boolean {
    return !!localStorage.getItem('jwt');  // check if token exists
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
    localStorage.removeItem('jwt'); // remove token
    this.closeMenu();
    this.router.navigate(['/login']); // redirect to login page
  }
}
