Feature: Report Export
  As a user
  I want to export my spending reports to PDF
  So that I can keep a permanent record of my finances

  Scenario: Successful PDF Export
    Given I have 5 expenses in my account
    When I generate a "Monthly" report for today
    And I click "Export to PDF"
    Then a PDF file "Expense_Report.pdf" should be created
    And the PDF should contain "Total Spending"
