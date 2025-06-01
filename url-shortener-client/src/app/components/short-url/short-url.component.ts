import { ErrorService } from './../../service/error/error.service';
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

  constructor(private urlService: UrlService,private errorService:ErrorService) {}

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
          this.errorService.showErrors(err);
        }
      });
  }

  handleRedirectRequest(shortCode: string) {
    this.urlService.redirectRequest(shortCode);
  }
}
