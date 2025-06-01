import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OriginalUrlComponent } from './original-url.component';

describe('OriginalUrlComponent', () => {
  let component: OriginalUrlComponent;
  let fixture: ComponentFixture<OriginalUrlComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OriginalUrlComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OriginalUrlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
