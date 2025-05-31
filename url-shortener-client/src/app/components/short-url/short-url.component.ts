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
  error = '';

  constructor(private http: HttpClient,private urlService: UrlService) {}

  shorten() {

    const request: CreateShortUrlRequest = {
      url: this.originalUrl
    };

    this.urlService.shortenUrl(request)
      .subscribe({
        next: (res) => {
          this.shortUrl = res.shortUrl;
          this.error = '';
        },
        error: (err) => {
          this.error = 'Failed to shorten URL';
          console.error(err);
        }
      });
  }

  handleRedirectRequest(shortCode: string) {
    this.urlService.redirectRequest(shortCode);
  }
}
