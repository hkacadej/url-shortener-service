import { Component } from '@angular/core';
import { UrlService } from '../../service/url/url.service';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-original-url',
  imports: [ReactiveFormsModule ],
  templateUrl: './original-url.component.html',
  styleUrl: './original-url.component.css'
})
export class OriginalUrlComponent {
  urlForm: FormGroup;
  errors: string[] = [];
  constructor(private urlService: UrlService,private fb: FormBuilder) {
    this.urlForm = this.fb.group({
      shortUrl: [
        '',
        [
          Validators.required,
          Validators.pattern(/^http:\/\/localhost:8080\/r\/[a-zA-Z0-9]{8}$/)
        ]
      ]
    });
  }



  handleRedirectRequest() {

    if (this.urlForm.invalid) {
      this.errors = ["Invalid short Url"];
      return;
    }

    const url: string = this.urlForm.get('shortUrl')?.value;

    this.urlService.redirectRequest(url);
  }


}
