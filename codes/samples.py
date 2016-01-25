
////////////////// 
print
print '>>> Syntax tree'
a=10
b=12
c=13
d= a+b+c

////////////////// 
print
print '>>> polite addition'
b=1.__add__(2)
def new_method(self, that):
   return self+that
a.my_method= new_method
a.my_method(1)
a=object().this.and_that

////////////////  
print
print '>>> Structure syntax, and mapping'
def offset_value(offset):
	def new_function(input):
		return offset+input
	return new_function

def map(l, f):
	c = 0
	newl = list()
	while c < len(l):
		newl.append(f(l.get(c)))
		c=c+1
	return newl

ls =[ 1, 2, 3, 4,]
funobj = offset_value(1)
mp = map(ls, funobj)
print ls
print map(ls, funobj)
print map(ls, @x:x*2>6)

/////////////////////// 
print
print '>>> Object'
a= []             # variables are typeless
a= 'string value' 
a.b = 13          # member can be assigned at any time
print a, a.b

//////////////////////// 
print
print '>>> Message sending and selector'
print 100+200,                 # normal method
print 100.__add__(200),        # as member
print int(0).__add__(100,200), # as function call
a=100
print send(a,'__add__')(a,200) # message sending

/////////////////////// 
print
print '>>> Parameter passing'
def function2():
	print "called function"
def function(a):
	a.s=1
	a=3
	print 'value '+str(a)+' has been entered'
a=2
print a, a.s
function(a)
print a, a.s

a=2
print a.s

a.f2 = function2
a.f1 = function
a.f2()    // act as normal function call
a.f1()    // self reference is automatically applied
a.f1(100) // or may be ignored

////////////////////////// 
print
print '>>> Global'
x = 1000
def testglobal():
	print 'outer1: ', x,  
	def  inner():
		# tells function to refer to 'x'
		#   from top level instead
		global x
		print ' inner1: ', x, ' adding 1'
		x = x + 1  # now allows assignment
	return  inner

def testglobal2():
	global x           
	print 'outer2: ', x,
	def  inner():
		# one way street, can refer to 
		#   variables in testglobal2, but may 
		#   not modify them.  global goes
		#   directly to top level 
		print ' inner2: ', x
	x = x + 1  # add one to check inner.
	return  inner


a=testglobal(); a()
b=testglobal2(); b()
x=1002
testglobal()()
testglobal2()()
a()
b()

///////////////////////// 
print
print '>>> First class function'
def add_offset(offset):
   # values are bounded dynamically
   #   when new_function function 
   #   object is created every time
   #   outer class is called.
   def new_adder(input):
      return offset+input
   return new_adder

def map(l, f):
   # Passing function as parameter
   c = 0
   newl = list()
   while c < len(l):
      # perform value mapping with 
      #   function given
      newl.append(f(l.get(c)))
      c=c+1
   return newl

ls =[ 1, 2, 3, 4, 5]
funobj1 = add_offset(1)
funobj2 = add_offset(10)
print 'input=',ls        
print map(ls, funobj1)
print map(ls, funobj2)

# lastly, a lambda expression.
print map(ls, @x:x*2>6)

//////////////////////// 
print
print '>>> Operator overloading and reflection'
a=object()
print type(a), dir(a)
a.new_member=10
print dir(a)

def myInt(a):
	newInt= int(a)
	def new_add(v1,v2):
		result=str(v1)+' + '+ str(v2)
		result.__add__=new_add
		return result
	newInt.__add__=new_add
	return newInt

a=myInt(1); b=2; c=3; d=4; e=5.5
print a+b+c*d+e
a.__add__=super(a,'__add__')
print a+b+c*d+e

//////////////////// 
print
print '>>> Continuation, simulated'
def fibonacci(upper_bound):       # Using list to calcluate fibonacci series
   ar = list() 	
   ar.counter=0
   def next(self):                # simulating generator call
      if upper_bound < self.counter : 
         return False
      
      if self.counter < 2: 
         self.append(1)
      else: 
         self.append(self.get(-1) + self.get(-2))
      self.yield = self.get(-1)
      self.counter = self.counter + 1
      return True

   ar.has_next=next
   return ar

fibgen = fibonacci(5)
while fibgen.has_next():          
	print fibgen.yield,
print
print fibgen


/////////////////////// 
print
print '>>> Simple arithmatic parsing'
input = '3 + 4 + 5 * 6 + 7'

def SimpleParser():
	obj = object()
	def Node(value):
		
		def print_value(self): print self,
		value.print_value=print_value

		def in_order(self):
			if self.left: self.left.in_order()
			self.print_value()
			if self.right: self.right.in_order()
			
		def pre_order(self):
			self.print_value()
			if self.left: self.left.pre_order()
			if self.right: self.right.pre_order()
			
		def post_order(self):
			if self.left: self.left.post_order()
			if self.right: self.right.post_order()
			self.print_value()
		value.pre_order=pre_order; value.in_order=in_order; value.post_order=post_order
		return value
		
	def process_input(self,input):
		input=input.split(' ')
		while len(input):
			tok = input.remove(0)
			node = Node(tok)
			if not self.head:
				self.head=node
			elif tok=='+':
				node.left=self.head; self.head=node
			elif tok=='*':
				if self.head.right:
					node.left=self.head.right
					self.head.right=node
				else:
					node.left=self.head; self.head=node
			else:
				if self.head.right:
					self.head.right.right=node
				else:
					self.head.right=node
	obj.process_input=process_input
	return obj

parser=SimpleParser()
print 'input = "'+input+'"'
parser.process_input(input)
parser.head.in_order(); print
parser.head.pre_order(); print
parser.head.post_order(); print
