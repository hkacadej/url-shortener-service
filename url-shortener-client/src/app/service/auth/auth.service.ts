import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthResponse } from '../../common/auth-response';
import { AuthRequest } from '../../common/auth-request';
import { RegisterRequest } from '../../common/register-request';
import { environment } from '../../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiBaseUrl}${environment.authApi}`;

  constructor(private http: HttpClient) {}

  login(request: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}${environment.loginEndpoint}`,request);
  }

  register(request: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}${environment.registerEndpoint}`, request);
  }

}
