import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminEditOffer } from './admin-edit-offer';

describe('AdminEditOffer', () => {
  let component: AdminEditOffer;
  let fixture: ComponentFixture<AdminEditOffer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminEditOffer],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminEditOffer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
