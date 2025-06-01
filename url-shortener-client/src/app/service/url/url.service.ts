import { CreateShortUrlRequest } from './../../common/short-url-request';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Url } from '../../common/url';
import { Observable } from 'rxjs';
import { CreateShortUrlResponse } from '../../common/short-url-response';
import { ErrorService } from '../error/error.service';
import { environment } from '../../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class UrlService {

  private apiUrl = `${environment.apiBaseUrl}${environment.urlApi}`;

  constructor(private http: HttpClient,private errorService: ErrorService) {}

  redirectRequest(shortCode: string) {
    console.log(shortCode)
    this.http.get<Url>(shortCode)
    .subscribe({
      next: (data) => {
        window.open(data.originalUrl, '_blank');
      },
      error: (err) => {
        console.error(err);
        this.errorService.showErrors(err);
      },
    });
  }

  getUrls(): Observable<Url[]> {
    return this.http.get<Url[]>(
      `${this.apiUrl}${environment.urlListEndpoint}`);
  }

  shortenUrl(request:CreateShortUrlRequest): Observable<CreateShortUrlResponse>{
    return this.http.post<CreateShortUrlResponse>( `${this.apiUrl}${environment.shortenEndpoint}`, request);
  }

}
