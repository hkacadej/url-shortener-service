import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RegisterRequest } from '../../common/register-request';
import { AuthService } from '../../service/auth/auth.service';
import { FormsModule } from '@angular/forms';

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
  error = '';
  success = '';

  constructor(private authService: AuthService, private router: Router) {}

  register() {
    const request: RegisterRequest = {
      email: this.email,
      name: this.name,
      password: this.password
    };

    this.authService.register(request).subscribe({
      next: () => {
        this.success = 'Registration successful!';
        this.error = '';
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.error = err.error?.message || 'Registration failed';
        this.success = '';
      }
    });
  }
}
