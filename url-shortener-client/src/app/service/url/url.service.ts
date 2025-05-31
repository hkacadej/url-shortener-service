import { CreateShortUrlRequest } from './../../common/short-url-request';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Url } from '../../common/url';
import { Observable } from 'rxjs';
import { CreateShortUrlResponse } from '../../common/short-url-response';

@Injectable({
  providedIn: 'root'
})
export class UrlService {

  constructor(private http: HttpClient) {}

  redirectRequest(shortCode: string) {
    this.http.get<Url>(shortCode)
    .subscribe({
      next: (data) => {
        window.open(data.originalUrl, '_blank');
      },
      error: (err) => {
        //alert('Could not open URL');
        console.error(err);
      },
    });
  }

  getUrls(): Observable<Url[]> {
    return this.http.get<Url[]>('http://localhost:8080/api/v1/urls');
  }

  shortenUrl(request:CreateShortUrlRequest): Observable<CreateShortUrlResponse>{
    return this.http.post<CreateShortUrlResponse>('http://localhost:8080/api/v1/shorten', request);
  }

}
