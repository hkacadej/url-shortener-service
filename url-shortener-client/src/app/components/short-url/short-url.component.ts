import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CreateShortUrlRequest } from '../../common/short-url-request';
import { CreateShortUrlResponse } from '../../common/short-url-response';
import { UrlService } from '../../service/url/url.service';

@Component({
  selector: 'app-short-url',
  imports: [FormsModule],
  templateUrl: './short-url.component.html',
  styleUrl: './short-url.component.css'
})
export class ShortUrlComponent {

  originalUrl = '';
  shortUrl = '';
  errors :String[] = [];

  constructor(private http: HttpClient,private urlService: UrlService) {}

  shorten() {

    const request: CreateShortUrlRequest = {
      url: this.originalUrl
    };

    this.urlService.shortenUrl(request)
      .subscribe({
        next: (res) => {
          this.shortUrl = res.shortUrl;
          this.errors = [];
        },
        error: (err) => {
          if (err.error && typeof err.error === 'object') {
            this.errors = Object.values(err.error);
          } else if (typeof err.error === 'string') {
            this.errors = [err.error];
          } else {
            this.errors = ['Failed to shorten URL'];
          }
        }
      });
  }

  handleRedirectRequest(shortCode: string) {
    this.urlService.redirectRequest(shortCode);
  }
}
