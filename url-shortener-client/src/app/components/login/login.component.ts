import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth/auth.service';
import { AuthRequest } from '../../common/auth-request';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  email = '';
  password = '';
  errors :String[] = [];

  constructor(private authService:AuthService,private router : Router ){}

  login() {
    const request: AuthRequest = {
      email : this.email,
      password: this.password
    }

    this.authService.login(request).subscribe({
      next: (response) => {
        localStorage.setItem('jwt', response.accessToken);
        this.router.navigate(['/urls']);
      },
      error: (err) => {
        if (err.error && typeof err.error === 'object') {
          this.errors = Object.values(err.error);
        } else if (typeof err.error === 'string') {
          this.errors = [err.error];
        } else {
          this.errors = ['Invalid email or password'];
        }
      }
    });

  }
}
