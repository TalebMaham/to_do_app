module.exports = {
    preset: 'jest-preset-angular',
    setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
    testEnvironment: 'jsdom',
    moduleNameMapper: {
      '@app/(.*)': '<rootDir>/src/app/$1',
      '@environments/(.*)': '<rootDir>/src/environments/$1'
    }
  };
  