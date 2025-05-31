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

  constructor(private urlService: UrlService) {}

  ngOnInit(): void {

    this.urlService.getUrls()
      .subscribe({
        next: (data) => {
          this.urls = data;
        },
        error: (err) => {
          console.error(err);
        },
      });
  }

  handleRedirectRequest(shortCode: string) {
    this.urlService.redirectRequest(shortCode);
  }

}
