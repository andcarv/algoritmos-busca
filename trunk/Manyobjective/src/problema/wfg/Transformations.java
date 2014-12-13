//  Transformations.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package problema.wfg;

/**
 * Class implementing the basics transformations for WFG
 */
public class Transformations {
        
  
    
  /**
   * b_poly transformation
   * @throws JMException 
   */
  public double b_poly(double y, double alpha) throws Exception{
    if (!(alpha>0)) {
      Class<String> cls = java.lang.String.class;
      String name = cls.getName(); 
      throw new Exception("Exception in " + name + ".b_poly()") ; 
    }
        
    return correct_to_01((double)StrictMath.pow(y,alpha));
  } //b_poly
    
  
  /**
   * b_flat transformation
   */
  public double b_flat(double y, double A, double B, double C){    
    double tmp1 = Math.min((double)0, (double)Math.floor(y - B))* A*(B-y)/B;
    double tmp2 = Math.min((double)0, (double)Math.floor(C - y))* (1 - A)*(y - C)/(1 - C);
        
    return correct_to_01(A + tmp1 - tmp2);
  }  // b_flat      
    
  /**
   * s_linear transformation
   */
  public double s_linear(double y, double A){
    return correct_to_01(Math.abs(y - A) /(double)Math.abs(Math.floor(A - y) + A));
  } // s_linear
    
  /**
   * s_decept transformation
   */
  public double s_decept(double y, double A, double B, double C){        
    double tmp, tmp1, tmp2;
        
    tmp1 = (double)Math.floor(y - A + B) * ((double)1.0 - C + (A - B)/B) / (A - B);
    tmp2 = (double)Math.floor(A + B - y) * ((double)1.0 - C + ((double)1.0 - A - B) / B) / ((double)1.0 - A - B);
        
    tmp = Math.abs(y - A) - B;
        
    return correct_to_01((double)1 + tmp * (tmp1 + tmp2 + (double)1.0/B));
  } // s_decept
    
  /**
   * s_multi transformation
   */
  public double s_multi(double y, int A, int B, double C){                
    double tmp1, tmp2;
        
    tmp1 = ((double)4.0 * A + (double)2.0) *
           (double)Math.PI * 
           ((double)0.5 - Math.abs(y - C) /((double)2.0 * ((double)Math.floor(C - y) + C)));
    tmp2 = (double)4.0 * B * 
           (double)StrictMath.pow(Math.abs(y - C) /((double)2.0 * ((double)Math.floor(C - y) + C))
                                 ,(double)2.0);
        
    return correct_to_01(((double)1.0 + (double)Math.cos(tmp1) + tmp2) / (B + (double)2.0));
  } //s_multi
    
  /**
   * r_sum transformation
   */
  public double r_sum(double [] y, double [] w){
    double tmp1 = (double)0.0, tmp2 =(double) 0.0;
    for (int i = 0; i < y.length; i++){
      tmp1 += y[i]*w[i];
      tmp2 += w[i];
    }
        
    return correct_to_01(tmp1 / tmp2);
  } // r_sum  
    
  /**
   * r_nonsep transformation
   */
  public double r_nonsep(double [] y, int A){               
    double tmp, denominator, numerator;
      
    tmp = (double)Math.ceil(A/(double)2.0);        
    denominator = y.length * tmp * ((double)1.0 + (double)2.0*A - (double)2.0*tmp)/A;        
    numerator = (double)0.0;
    for (int j = 0; j < y.length; j++){
      numerator += y[j];
      for (int k = 0; k <= A-2; k++){
        numerator += Math.abs( y[j] - y[( j+k+1 ) % y.length]);
      }
    }
        
    return correct_to_01(numerator/denominator);
  } // r_nonsep
    
  /**
   * b_param transformation
   */
  public double b_param(double y, double u, double A, double B, double C){
    double result, v, exp;
        
    v = A - ((double)1.0 - (double)2.0 * u) *
            Math.abs((double)Math.floor((double)0.5 - u) + A);
    exp = B + (C - B)*v;        
    result = (double)StrictMath.pow(y,exp);
        
    return correct_to_01(result);                  
  } // b_param
    
  /**
   */
  double correct_to_01(double a){    
    double min = (double)0.0;
    double max = (double)1.0;
    double min_epsilon_neg = min - WFG.epsilon;
    double min_epsilon_pos = min + WFG.epsilon;
    double max_epsilon_neg = max - WFG.epsilon;
    double max_epsilon_pos = max + WFG.epsilon;
    
    if (( a >= min_epsilon_neg && a <= min) || (a >= min && a <= min_epsilon_pos)) {
      return min;        
    } else if (( a >= max_epsilon_neg && a <= max ) || (a >= max && a <= max_epsilon_pos)) {
      return max;        
    } else {
      return a;        
    }
  } // correct_to_01      
} // Transformations
