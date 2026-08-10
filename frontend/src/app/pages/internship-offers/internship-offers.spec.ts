import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InternshipOffers } from './internship-offers';

describe('InternshipOffers', () => {
  let component: InternshipOffers;
  let fixture: ComponentFixture<InternshipOffers>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InternshipOffers],
    }).compileComponents();

    fixture = TestBed.createComponent(InternshipOffers);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
