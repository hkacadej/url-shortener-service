import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Url } from '../../common/url';
import { UrlService } from '../../service/url/url.service';

@Component({
  selector: 'app-url-list',
  imports: [],
  templateUrl: './url-list.component.html',
  styleUrl: './url-list.component.css'
})
export class UrlListComponent implements OnInit {
  urls: Url[] = [];
  error: string | null = null;

  constructor(private urlService: UrlService) {}

  ngOnInit(): void {

    this.urlService.getUrls()
      .subscribe({
        next: (data) => {
          this.urls = data;
        },
        error: (err) => {
          this.error = 'Failed to fetch URLs.';
          console.error(err);
        },
      });
  }

  handleRedirectRequest(shortCode: string) {
    this.urlService.redirectRequest(shortCode);
  }

}
