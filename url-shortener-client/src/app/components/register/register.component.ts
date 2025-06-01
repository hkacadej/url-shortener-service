import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RegisterRequest } from '../../common/register-request';
import { AuthService } from '../../service/auth/auth.service';
import { FormsModule } from '@angular/forms';
import { TokenService } from '../../service/token/token.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  email = '';
  name = '';
  password = '';
  errors :String[] = [];
  success = '';

  constructor(private authService: AuthService,
              private router: Router,
              private tokenService: TokenService
            ) {}

  register() {
    const request: RegisterRequest = {
      email: this.email,
      name: this.name,
      password: this.password
    };

    this.authService.register(request).subscribe({
      next: (data) => {
        this.tokenService.saveToken(data.accessToken)
        this.router.navigate(['/urls']);
      },
      error: (err) => {
        if (err.error && err.error.details && typeof err.error.details === 'string') {
          this.errors = [err.error.details];
        }else if (err.error && typeof err.error === 'object') {
          this.errors = Object.values(err.error);
        } else if (typeof err.error === 'string') {
          this.errors = [err.error];
        } else {
          this.errors = ['Failed to shorten URL'];
        }
      }
    });
  }
}
