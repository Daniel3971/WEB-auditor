import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminCreateOffer } from './admin-create-offer';

describe('AdminCreateOffer', () => {
  let component: AdminCreateOffer;
  let fixture: ComponentFixture<AdminCreateOffer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminCreateOffer],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminCreateOffer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
